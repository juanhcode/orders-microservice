package com.develop.orders_microservice.application.use_cases;

import com.develop.orders_microservice.application.dtos.PurchaseDto;
import com.develop.orders_microservice.application.dtos.PurchaseRequestDto;
import com.develop.orders_microservice.application.dtos.PurchaseResponseDto;
import com.develop.orders_microservice.domain.interfaces.PurchaseService;
import com.develop.orders_microservice.domain.models.Purchase;
import com.develop.orders_microservice.domain.models.PurchaseProduct;
import com.develop.orders_microservice.infraestructure.clients.DeliveryClientRest;
import com.develop.orders_microservice.infraestructure.clients.UsersClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.Delivery;
import com.develop.orders_microservice.infraestructure.clients.models.Users;
import com.develop.orders_microservice.infraestructure.clients.PaymentStatusClientRest;
import com.develop.orders_microservice.infraestructure.clients.models.PaymentStatus;
import com.develop.orders_microservice.infraestructure.messaging.SnsService;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseProductRepository;
import com.develop.orders_microservice.infraestructure.repositories.PurchaseRepository;
import com.develop.orders_microservice.presentation.exceptions.BadRequestException;
import com.develop.orders_microservice.presentation.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PaymentStatusClientRest paymentStatusClientRest;
    private final SnsService snsService;
    private final UsersClientRest usersClientRest;
    private final PurchaseProductRepository purchaseProductRepository;
    private final DeliveryClientRest deliveryClientRest;

    public PurchaseServiceImpl
            (
            PurchaseRepository purchaseRepository,
            PaymentStatusClientRest paymentStatusClientRest,
            SnsService snsService,
            UsersClientRest usersClientRest,
            PurchaseProductRepository purchaseProductRepository,
            DeliveryClientRest deliveryClientRest
            )
    {
        this.purchaseRepository = purchaseRepository;
        this.paymentStatusClientRest = paymentStatusClientRest;
        this.snsService = snsService;
        this.usersClientRest =  usersClientRest;
        this.purchaseProductRepository = purchaseProductRepository;
        this.deliveryClientRest = deliveryClientRest;
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
    public List<PurchaseResponseDto> getPurchasesByUserId(Integer userId) {
        Optional<Users> userOptional = usersClientRest.getUser(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        // Buscar las compras por userId
        List<Purchase> purchases = purchaseRepository.findByUserId(userId);
        if (purchases.isEmpty()) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId);
        }
        System.out.println("Los deliveryId de las orders son: " + purchases.stream().map(Purchase::getDeliveryId).collect(Collectors.toList()));
        // Obtener los deliverys asociados a las compras
        List<Delivery> deliveries = purchases.stream()
                .map(purchase -> deliveryClientRest.getDeliveryById(purchase.getDeliveryId()))
                .collect(Collectors.toList());
        // Combinar las compras y los deliverys en PurchaseResponseDto
        List<PurchaseResponseDto> purchaseResponseDtos = purchases.stream().map(purchase -> {
            Delivery delivery = deliveries.stream()
                    .filter(d -> d.getId().equals(purchase.getDeliveryId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Delivery not found for purchase with id: " + purchase.getOrderId()));
            PurchaseResponseDto purchaseResponseDto = new PurchaseResponseDto();
            purchaseResponseDto.setOrderId(Long.valueOf(purchase.getOrderId()));
            purchaseResponseDto.setUserId(Long.valueOf(purchase.getUserId()));
            purchaseResponseDto.setDeliveryAddress(purchase.getDeliveryAddress());
            purchaseResponseDto.setPaymentTypeId(purchase.getPaymentTypeId());
            purchaseResponseDto.setPaymentStatusId(purchase.getPaymentStatusId());
            purchaseResponseDto.setDeliveryName(delivery.getStatus());
            purchaseResponseDto.setTotal(purchase.getTotal());
            return purchaseResponseDto;
        }).collect(Collectors.toList());
        return purchaseResponseDtos;
    }

    @Override
    public PurchaseResponseDto getPurchaseByUserIdAndOrderId(Integer userId, Integer orderId) {
        // Verificar si el usuario existe y si la orden existe
        Optional<Users> userOptional = usersClientRest.getUser(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Optional<Purchase> purchaseOptional = purchaseRepository.findById(orderId);
        if (purchaseOptional.isEmpty()) {
            throw new ResourceNotFoundException("Purchase not found");
        }
        Delivery delivery = deliveryClientRest.getDeliveryById(purchaseOptional.get().getDeliveryId());
        if (delivery == null) {
            throw new ResourceNotFoundException("Delivery not found for purchase with id: " + orderId);
        }
        // Buscar el orden por userId y orderId
        Purchase purchases = purchaseRepository.findByUserIdAndOrderId(userId, orderId);
        if (purchases == null) {
            throw new ResourceNotFoundException("No purchases found for user with id: " + userId + " and order id: " + orderId);
        }
        // Combinar el purchase obtenido y el deliveryName en un PurchaseResponseDto
        PurchaseResponseDto purchaseResponseDto = new PurchaseResponseDto();
        purchaseResponseDto.setOrderId(Long.valueOf(purchases.getOrderId()));
        purchaseResponseDto.setUserId(Long.valueOf(purchases.getUserId()));
        purchaseResponseDto.setDeliveryAddress(purchases.getDeliveryAddress());
        purchaseResponseDto.setPaymentTypeId(purchases.getPaymentTypeId());
        purchaseResponseDto.setPaymentStatusId(purchases.getPaymentStatusId());
        purchaseResponseDto.setDeliveryName(delivery.getStatus());
        purchaseResponseDto.setTotal(purchases.getTotal());

        return purchaseResponseDto;

    }

    @Override
    public Purchase savePurchase(PurchaseRequestDto purchaseRequest) {
        //Crear delivery por defecto
        Delivery defafultDelivery = new Delivery();
        defafultDelivery.setDelivered(false);
        defafultDelivery.setStatusId(2L);
        defafultDelivery.setUserId(Long.valueOf(purchaseRequest.getUserId()));

        Delivery savedDelivery = deliveryClientRest.createDelivery(defafultDelivery);

        // Crear la compra principal
        Purchase purchase = new Purchase();
        purchase.setUserId(purchaseRequest.getUserId());
        purchase.setDeliveryAddress(purchaseRequest.getDeliveryAddress());
        purchase.setPaymentTypeId(purchaseRequest.getPaymentTypeId());
        purchase.setPaymentStatusId(purchaseRequest.getPaymentStatusId());
        purchase.setDeliveryId(savedDelivery.getId());

        // Calcular total
        BigDecimal total = purchaseRequest.getProducts().stream()
                .map(p -> BigDecimal.valueOf(p.getTotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setTotal(total);

        // Guardar la compra principal
        Purchase savedPurchase = purchaseRepository.save(purchase);

        // Guardar los productos
        List<PurchaseProduct> products = purchaseRequest.getProducts().stream()
                .map(p -> {
                    PurchaseProduct pp = new PurchaseProduct();
                    pp.setPurchaseId(savedPurchase.getOrderId());
                    pp.setProductId(p.getProductId().intValue()); // Convertir Long a Integer
                    pp.setQuantity(p.getQuantity());
                    pp.setTotal(BigDecimal.valueOf(p.getTotal()));
                    return pp;
                })
                .collect(Collectors.toList());

        purchaseProductRepository.saveAll(products);

        // Notificación SNS
        PaymentStatus paymentStatus = paymentStatusClientRest.getPaymentStatusNameById(purchase.getPaymentStatusId());
        PurchaseDto purchaseDto = new PurchaseDto(
                savedPurchase.getOrderId(),
                savedPurchase.getUserId(),
                paymentStatus.getName(),
                purchaseRequest.getProducts().stream()
                        .map(p -> p.getProductId().intValue())
                        .collect(Collectors.toList())
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String purchaseDtoJson = objectMapper.writeValueAsString(purchaseDto);
            snsService.publishMessage(purchaseDtoJson);
        } catch (Exception e) {
            System.out.println("Error al convertir la compra a JSON: " + e.getMessage());
            throw new RuntimeException("Error al publicar el mensaje en SNS", e);
        }
        return savedPurchase;
    }


    @Override
    public Purchase updatePurchase(Integer orderId, PurchaseRequestDto purchaseRequest) {
        // Validar los productos primero
        if (purchaseRequest.getProducts() == null || purchaseRequest.getProducts().isEmpty()) {
            throw new BadRequestException("Products list cannot be empty");
        }

        // Obtener la orden existente
        Purchase existingPurchase = getPurchaseById(orderId);

        // Actualizar los campos básicos
        existingPurchase.setUserId(purchaseRequest.getUserId());
        existingPurchase.setDeliveryAddress(purchaseRequest.getDeliveryAddress());
        existingPurchase.setPaymentTypeId(purchaseRequest.getPaymentTypeId());
        existingPurchase.setPaymentStatusId(purchaseRequest.getPaymentStatusId());

        // Calcular y validar el nuevo total
        BigDecimal total;
        try {
            total = purchaseRequest.getProducts().stream()
                    .map(p -> {
                        if (p.getTotal() == null) {
                            throw new BadRequestException("Product total cannot be null for product: " + p.getProductId());
                        }
                        return BigDecimal.valueOf(p.getTotal());
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (NullPointerException e) {
            throw new BadRequestException("Invalid product data: " + e.getMessage());
        }

        existingPurchase.setTotal(total);

        // Guardar la orden actualizada
        Purchase updatedPurchase = purchaseRepository.save(existingPurchase);

        // Eliminar los productos antiguos
        purchaseProductRepository.deleteByPurchaseId(orderId);

        // Guardar los nuevos productos con validación
        List<PurchaseProduct> products = purchaseRequest.getProducts().stream()
                .map(p -> {
                    if (p.getProductId() == null || p.getQuantity() == null || p.getTotal() == null) {
                        throw new BadRequestException("Invalid product data: all fields are required");
                    }

                    PurchaseProduct pp = new PurchaseProduct();
                    pp.setPurchaseId(orderId);
                    pp.setProductId(p.getProductId().intValue());
                    pp.setQuantity(p.getQuantity());
                    pp.setTotal(BigDecimal.valueOf(p.getTotal()));
                    return pp;
                })
                .collect(Collectors.toList());

        purchaseProductRepository.saveAll(products);

        // Notificación SNS
        try {
            PaymentStatus paymentStatus = paymentStatusClientRest.getPaymentStatusNameById(purchaseRequest.getPaymentStatusId());
            PurchaseDto purchaseDto = new PurchaseDto(
                    orderId,
                    purchaseRequest.getUserId(),
                    paymentStatus.getName(),
                    purchaseRequest.getProducts().stream()
                            .map(p -> p.getProductId().intValue())
                            .collect(Collectors.toList())
            );

            ObjectMapper objectMapper = new ObjectMapper();
            String purchaseDtoJson = objectMapper.writeValueAsString(purchaseDto);
            snsService.publishMessage(purchaseDtoJson);
        } catch (Exception e) {
            // Log the error but don't fail the operation
            System.err.println("Error sending SNS notification: " + e.getMessage());
        }

        return updatedPurchase;
    }

    @Override
    public void deletePurchase(Integer purchaseId) {
        purchaseRepository.deleteById(purchaseId);
    }
}
