package fon.bank.transactionservice.service;

import fon.bank.transactionservice.entity.TransactionPK;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fon.bank.transactionservice.dao.TransactionRepository;
import fon.bank.transactionservice.dto.TransactionDTO;
import fon.bank.transactionservice.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionImpl {

    private TransactionRepository transactionRepository;
    private ModelMapper modelMapper;

    @Autowired
    public TransactionImpl(TransactionRepository transactionRepository,
                           ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

//    @Override
//    public List<TransactionDTO> findAll() {
//        return transactionRepository.findAll().stream().map(transaction->modelMapper.map(transaction, TransactionDTO.class))
//                .collect(Collectors.toList());
//    }

    public TransactionDTO findById(Object id) throws Exception {
        TransactionPK transactionPK = (TransactionPK) id;
        Optional<Transaction> transaction = transactionRepository.findById(transactionPK);
        return transaction.map(value -> modelMapper.map(value, TransactionDTO.class)).orElse(null);
    }

    public List<TransactionDTO> findBySenderId(String senderId){

        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getTransactionPK().getSender().equals(senderId))
                .map(transaction->modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    public List<TransactionDTO> findByReceiverId(String receiverId){

        return transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getTransactionPK().getReceiver().equals(receiverId))
                .map(transaction->modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    @Transactional
    public TransactionDTO save(TransactionDTO transactionDTO) throws Exception {

        if (transactionDTO == null) {
            throw new NullPointerException("Transakcija ne moze biti null");
        }

//        Transaction transaction = modelMapper.map(transactionDTO,Transaction.class);
//        if (transactionDTO.getSenderDTO() == null) {
//            throw new IllegalArgumentException("SenderDTO cannot be null");
//        }
//
//        if (transactionDTO.getReceiverDTO() == null) {
//            throw new IllegalArgumentException("ReceiverDTO cannot be null");
//        }
//        transaction.setSender(modelMapper.map(transactionDTO.getSenderDTO(), Account.class));
//        transaction.setReceiver(modelMapper.map(transactionDTO.getReceiverDTO(), Account.class));
//        Transaction savedTransaction = transactionRepository.save(transaction);
//        transaction.getTransactionPK().setTransactionid(savedTransaction.getTransactionPK().getTransactionid());
//        if(transaction.getSender() != null){
//            Account senderAcc = transaction.getSender();
//            senderAcc.setBalance(senderAcc.getBalance()-transaction.getAmount());
//
//            System.out.println(transaction.getSender().getClient());
//            senderAcc.setClient(transaction.getSender().getClient());
//            senderAcc.getSentTransactions().add(transaction);
//
//            AccountDTO accDTOSender = modelMapper.map(senderAcc, AccountDTO.class);
//            accDTOSender.setClientDTO(modelMapper.map(transaction.getSender().getClient(), ClientDTO.class));
//            accountImpl.save(accDTOSender);
//
//        }else{
//
//            System.out.println("Sender je prazan!");
//        }
//
//        if(transaction.getReceiver() != null){
//
//            AccountDTO receiverAccDTO = accountImpl.findById(transaction.getReceiver().getId());
//            if(receiverAccDTO != null){
//                Account receiverAcc = modelMapper.map(receiverAccDTO, Account.class);
//                if(!receiverAcc.getCurrency().equals(transaction.getSender().getCurrency())){
//
//                }
//                Client cl = modelMapper.map(receiverAccDTO.getClientDTO(), Client.class);
//                receiverAcc.setClient(cl);
//                receiverAcc.setBalance(receiverAcc.getBalance()+transaction.getAmount());
//
//                receiverAcc.getReceivedTransactions().add(transaction);
//
//                AccountDTO accDTOSender = modelMapper.map(receiverAcc, AccountDTO.class);
//                accDTOSender.setClientDTO(receiverAccDTO.getClientDTO());
//                accountImpl.save(accDTOSender);
//            }else{
//                accountImpl.save(modelMapper.map(transaction.getReceiver(), AccountDTO.class));
//            }
//        }
//        transactionPK.setTransactionid(6);
//        transactionPK.setSender(transaction.getTransactionPK().getSender());
//        transactionPK.setReceiver(transaction.getTransactionPK().getReceiver());
//        transaction.setTransactionPK(transactionPK);

//        return modelMapper.map(savedTransaction, TransactionDTO.class);
        return null;
    }

}
