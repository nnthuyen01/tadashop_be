package com.tadashop.nnt.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tadashop.nnt.utils.constant.StateOrderConstant;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "_order")
public class Order extends AbstractEntity {

	@Column(name = "state", nullable = false)
	@Enumerated(EnumType.STRING)
	private StateOrderConstant state;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "create_date")
	private LocalDateTime createTime;

	@Column(name ="delivery_address", nullable = false)
	private String deliveryAddress;
	
	@Column(name ="receiver_phone")
	private String receiverPhone;
	
	@Column(name ="receiver_name")
	private String receiverName;
	
	@Column(name = "total_quantity", nullable = false)
	private Integer totalQuantity;
	
	@Column(name = "total_price",  nullable = false)
	private Double totalPrice;
	
	@Column(name ="discount_code")
	private String discountCode;
	
	@Column(name ="price_off")
	private Double priceOff;
	
	@Column(name ="note")
	private String note;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_User_id")
	private User orderUser;
	
	@JsonIgnore
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrderDetail> orderDetails;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;
	
	public void setState(int stateValue) {
	    // Map integer values to StateOrderConstant enum values
	    switch (stateValue) {
	        case 0:
	            this.state = StateOrderConstant.Pending;
	            break;
	        case 1:
	            this.state = StateOrderConstant.Processing;
	            break;
	        case 2:
	            this.state = StateOrderConstant.Complete;
	            break;
	        case 3:
	            this.state = StateOrderConstant.Cancel;
	            break;
	        case 4:
	            this.state = StateOrderConstant.Delivery;
	            break;
	        case 5:
	            this.state = StateOrderConstant.Paid;
	            break;
	        case 6:
	            this.state = StateOrderConstant.UnPaid;
	            break;
	        case 7:
	            this.state = StateOrderConstant.Confirmed;
	            break;
	        // Add cases for other integer values and enum values as needed
	        default:
	            // Handle invalid state values or throw an exception
	            throw new IllegalArgumentException("Invalid state value: " + stateValue);
	    }
	}
	public int getStateValue() {
        // Map StateOrderConstant enum values to integer values
        switch (this.state) {
            case Pending:
                return 0;
            case Processing:
                return 1;
            case Complete:
                return 2;
            case Cancel:
                return 3;
            case Delivery:
                return 4;
            case Paid:
                return 5;
            case UnPaid:
                return 6;
            case Confirmed:
                return 7;
            // Add cases for other enum values as needed
            default:
                // Handle invalid state values or throw an exception
                throw new IllegalArgumentException("Invalid state value: " + this.state);
        }
    }
}
