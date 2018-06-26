package com.domain.entity;

import com.domain.enums.StockOutPurpose;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "inventory_stock_out")
public class SotckOut implements Serializable{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commodityItemId;

    private Timestamp pickedTime;

    private Long pickedUser;

    private Long approvedUser;

    private StockOutPurpose purpose;

    @PrePersist
    public void prePersist() {
        if (null == pickedTime) {
            this.pickedTime = new Timestamp(System.currentTimeMillis());
        }
    }
}
