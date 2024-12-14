package com.nivlalulu.nnpro.common.mapping;

public interface IGenericMapper {
    /**
     * Provides best effort mapping of a source object to a destination object
     * @param from
     * @param toClass
     * @return
     * @param <TFrom>
     * @param <TTo>
     */
    <TFrom, TTo> TTo map(TFrom from, Class<TTo> toClass);
}
