package it.finanze.sanita.fse2.ms.gtw.config.logging;


public class NoKeyKeyingStrategy implements KeyingStrategy<Object> {

    @Override
    public byte[] createKey(Object e) {
        return null;
    }
} 