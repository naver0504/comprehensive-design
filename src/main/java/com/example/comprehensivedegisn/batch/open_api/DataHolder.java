package com.example.comprehensivedegisn.batch.open_api;

public interface DataHolder<T> {

    void init();
    Integer getDongEntityId(T t);
}
