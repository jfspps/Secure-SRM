package com.secure_srm.services;

import java.util.Set;

//BaseService and all child classes/interfaces are strictly not necessary when only impl JPA services but is provided here
//if databases or datasources other than MySQL are required

public interface BaseService<T, ID> {

    T save(T object);

    T findById(ID id);

    Set<T> findAll();

    void delete(T objectT);

    void deleteById(ID id);
}
