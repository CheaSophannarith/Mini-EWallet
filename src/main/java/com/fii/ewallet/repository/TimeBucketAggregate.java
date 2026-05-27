package com.fii.ewallet.repository;

import java.math.BigDecimal;

public interface TimeBucketAggregate {

    String getBucket();

    Long getCount();

    BigDecimal getVolume();

}
