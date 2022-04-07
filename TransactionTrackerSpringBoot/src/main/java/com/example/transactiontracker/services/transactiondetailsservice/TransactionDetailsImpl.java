package com.example.transactiontracker.services.transactiondetailsservice;

import com.example.transactiontracker.models.product.Product;
import com.example.transactiontracker.models.transaction.Transaction;
import com.example.transactiontracker.models.transaction.TransactionDetail;
import com.example.transactiontracker.models.transaction.TransactionType;
import com.example.transactiontracker.models.payload.dto.TransactionDetailsDTO;
import com.example.transactiontracker.models.payload.response.TransactionDetailResponse;
import com.example.transactiontracker.services.repositories.ProductRepository;
import com.example.transactiontracker.services.repositories.TransactionDetailsRepository;
import com.example.transactiontracker.services.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionDetailsImpl implements TransactionDetailsService {

    private final TransactionDetailsRepository transactionDetailsRepository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final String imageBaseURL = "//localhost:8080/images/";

    @Override
    public TransactionDetail save(TransactionDetail transactionDetail) {
        return transactionDetailsRepository.save(transactionDetail);
    }

    @Override
    public void deleteById(long id) {
        transactionDetailsRepository.deleteById(id);
    }

    @Override
    public Optional<TransactionDetail> findById(long id) {
        return transactionDetailsRepository.findById(id);
    }

    @Override
    public TransactionDetail setTransactionDetailsAttributesAndReturnNewEntity(TransactionDetailsDTO transactionDetailsDTO, TransactionDetail transactionDetailsData) {
        transactionDetailsData.setTransaction(transactionRepository.findById(transactionDetailsDTO.getTransactionId()).orElse(null));
        transactionDetailsData.setProduct(productRepository.findById(transactionDetailsDTO.getProductId()).orElse(null));
        transactionDetailsData.setPrice(transactionDetailsDTO.getPrice());
        transactionDetailsData.setQuantity(transactionDetailsDTO.getQuantity());
        return transactionDetailsData;
    }

    @Override
    public TransactionDetailResponse generateImageUrl(TransactionDetailResponse transactionDetailResponse) {
        if (!transactionDetailResponse.getProduct().getImage().startsWith("//")) {
            transactionDetailResponse.getProduct().setImage(imageBaseURL + transactionDetailResponse.getProduct().getImage());
        }
        return transactionDetailResponse;
    }

    @Override
    public void updateProductInventory(TransactionDetailsDTO transactionDetailsDTO) {
        Transaction transaction = transactionRepository.getById(transactionDetailsDTO.getTransactionId());
        Product product = productRepository.getById(transactionDetailsDTO.getProductId());
        int difference = transactionDetailsDTO.getQuantity();

        if (transactionDetailsDTO.getId() != null) {
            TransactionDetail prev = transactionDetailsRepository.getById(transactionDetailsDTO.getId());
            int prevQuantity = prev.getQuantity();
            difference = transactionDetailsDTO.getQuantity() - prevQuantity;
        }

        setProductQuantityDifference(transaction, product, difference);
        productRepository.save(product);
    }

    @Override
    public TransactionDetailResponse update(TransactionDetail transactionDetailsData, TransactionDetailsDTO transactionDetailsDTO) {
        updateProductInventory(transactionDetailsDTO);
        TransactionDetail transactionDetailEntity = setTransactionDetailsAttributesAndReturnNewEntity(transactionDetailsDTO, transactionDetailsData);
        save(transactionDetailEntity);
        TransactionDetailResponse response = new TransactionDetailResponse(transactionDetailEntity.getId(), transactionDetailEntity.getTransaction(),
                transactionDetailEntity.getProduct(), transactionDetailEntity.getQuantity(),
                transactionDetailEntity.getPrice());
        generateImageUrl(response);
        return response;
    }

    @Override
    public void saveAll(List<TransactionDetailsDTO> transactionDetailsDTOS) {
        List<TransactionDetail> transactionDetails = new ArrayList<>();
        Transaction transaction = transactionRepository.save(new Transaction(new Date(), TransactionType.SALE, 0));
        for (TransactionDetailsDTO transactionDetailsDTO : transactionDetailsDTOS) {
            transactionDetails.add(new TransactionDetail(transaction,
                    productRepository.getById(transactionDetailsDTO.getProductId()),
                    transactionDetailsDTO.getQuantity(), transactionDetailsDTO.getPrice()));
        }
        transactionDetailsRepository.saveAll(transactionDetails);
    }

    private void setProductQuantityDifference(Transaction transaction, Product product, int difference) {
        if (transaction.getTransactionType() == TransactionType.SALE) {
            product.setQuantity(product.getQuantity() - difference);
        } else if (transaction.getTransactionType() == TransactionType.RECEIVE) {
            product.setQuantity(product.getQuantity() + difference);
        }
    }
}


