package fon.bank.accountservice.service;

import java.util.List;

public interface ServiceInterface<T> {
    List<T> findAll();
}
