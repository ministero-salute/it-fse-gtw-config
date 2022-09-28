package it.finanze.sanita.fse2.ms.gtw.config.logging;

public interface FailedDeliveryCallback<E> {
	
    void onFailedDelivery(E evt, Throwable throwable);
    
}