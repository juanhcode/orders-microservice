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
    public List<Purchase> getPurchasesByUserId(Integer userId) {
        Optional<Users> userOptional = usersClientRest.getUser(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        return purchaseRepository.findByUserId(userId);
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

        String purchaseDtoJson = "{"
                + "orderId:" + purchaseDto.getOrderId() + ","
                + "userId:" + purchaseDto.getUserId() + ","
                + "paymentStatus:" + paymentStatus.getName()
                + "}";

        // Publicar el mensaje
        try {
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

    @Override
    public Optional<Purchase> getPurchaseById(Integer purchaseId) {
        return purchaseRepository.findById(purchaseId);
    }
}
