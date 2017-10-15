package com.xbyg_plus.silicon.data.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbyg_plus.silicon.data.factory.EntryFactory;

public abstract class ORMRepository<EntrySet, Entry, EF extends EntryFactory<Entry>> extends BaseDataRepository<EntrySet> {
    protected final ObjectMapper mapper = new ObjectMapper();
    protected EF entryFactory;

    protected ORMRepository(String storeName, EF entryFactory) {
        super(storeName);
        this.entryFactory = entryFactory;
    }
}
