package it.casatta.estimatefee;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;

/**
 * Created by Riccardo Casatta @RCasatta on 13/07/15.
 */
@Entity
@Cache
public class EstimatedFee implements Serializable {

    @Id
    private Long id;

    @Index
    private Long timestamp;

    @Index
    private String day;

    private Double estimateFee1Block;
    private Double estimateFee2Block;
    private Double estimateFee6Block;
    private Double estimateFee12Block;
    private Double estimateFee25Block;

    public Double getEstimateFee12Block() {
        return estimateFee12Block;
    }

    public void setEstimateFee12Block(Double estimateFee10Block) {
        this.estimateFee12Block = estimateFee10Block;
    }

    public Double getEstimateFee1Block() {
        return estimateFee1Block;
    }

    public void setEstimateFee1Block(Double estimateFee1Block) {
        this.estimateFee1Block = estimateFee1Block;
    }

    public Double getEstimateFee25Block() {
        return estimateFee25Block;
    }

    public void setEstimateFee25Block(Double estimateFee25Block) {
        this.estimateFee25Block = estimateFee25Block;
    }

    public Double getEstimateFee2Block() {
        return estimateFee2Block;
    }

    public void setEstimateFee2Block(Double estimateFee2Block) {
        this.estimateFee2Block = estimateFee2Block;
    }

    public Double getEstimateFee6Block() {
        return estimateFee6Block;
    }

    public void setEstimateFee6Block(Double estimateFee6Block) {
        this.estimateFee6Block = estimateFee6Block;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "EstimatedFee{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", day='" + day + '\'' +
                ", estimateFee1Block=" + estimateFee1Block +
                ", estimateFee2Block=" + estimateFee2Block +
                ", estimateFee6Block=" + estimateFee6Block +
                ", estimateFee12Block=" + estimateFee12Block +
                ", estimateFee25Block=" + estimateFee25Block +
                '}';
    }
}
