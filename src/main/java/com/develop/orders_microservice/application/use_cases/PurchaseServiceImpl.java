package com.develop.orders_microservice.application.use_cases;

import com.develop.orders_microservice.application.dtos.PurchaseDto;
import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.infraestructure.clients.UsersClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.Users;
import com.develop.orders_microservice.infraestructure.clients.PaymentStatusClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.PaymentStatus;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentStatusClientRest paymentStatusClientRest;
    private final SnsService snsService;
    private final UsersClientRest usersClientRest;

    public PurchaseServiceImpl
            (
            PurchaseRepository purchaseRepository,
            PaymentStatusClientRest paymentStatusClientRest,
            SnsService snsService,
            UsersClientRest usersClientRest
            )
    {
        this.purchaseRepository = purchaseRepository;
        this.paymentStatusClientRest = paymentStatusClientRest;
        this.snsService = snsService;
        this.usersClientRest =  usersClientRest;
    }

    @Override
    public Purchase getPurchaseById(Integer purchaseId) {
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(purchaseId);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found");
        }
        return purchaseOptional.get();
    }

    @Override
    public List<Purchase> getPurchasesByUserId(Integer userId) {
        Optional<Users> userOptional = usersClientRest.getUser(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return purchaseRepository.findByUserId(userId);
    }

    @Override
    public List<Purchase> getPurchasesByUserIdAndOrderId(Integer userId, Integer orderId) {
        // Verificar si el usuario existe y si la orden existe
        Optional<Users> userOptional = usersClientRest.getUser(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(orderId);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found");
        }

        // Obtener la lista de compras por userId y orderId
        List<Purchase> purchases = purchaseRepository.findByUserIdAndOrderId(userId, orderId);
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId + " and order id: " + orderId);
        }
        return purchases;
    }

    @Override
    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);

        PaymentStatus paymentStatus = paymentStatusClientRest.getPaymentStatusNameById(purchase.getPaymentStatusId());

//         Crear el DTO con el nombre del estado de pago
        PurchaseDto purchaseDto = new PurchaseDto(
                purchase.getOrderId(),
                purchase.getUserId(),
                paymentStatus.getName()
        );

        // Publicar el mensaje
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String purchaseDtoJson = objectMapper.writeValueAsString(purchaseDto);
            snsService.publishMessage(purchaseDtoJson);
        } catch (Exception e) {
            System.out.println("Error al convertir la compra a JSON: " + e.getMessage());
            throw new RuntimeException("Error al publicar el mensaje en SNS", e);
        }
    }

    @Override
    public void deletePurchase(Integer purchaseId) {
        purchaseRepository.deleteById(purchaseId);
    }
}
