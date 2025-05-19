package com.example.agrishop;

public class OrderModel {
 private String orderId;
 private String documentId;
 private String userId;
 private String username;
 private String address;
 private String mobilenumber;
 private String imageUrl;
 private String orderDateTime;
 private String orderTime;
 private String orderName; // Assuming it's product name
 private int orderPrice;
 private int orderQuantity;
 private String orderStatus;
 private String orderWeight;


 public OrderModel() {
  // Default constructor required for Firestore
 }

 public OrderModel(String orderId, String documentId, String userId, String username, String address,
                   String mobilenumber, String imageUrl, String orderDateTime, String orderTime,
                   String orderName, int orderPrice, int orderQuantity,String orderStatus,String orderWeight) {
  this.orderId = orderId;
  this.documentId = documentId;
  this.userId = userId;
  this.username = username;
  this.address = address;
  this.mobilenumber = mobilenumber;
  this.imageUrl = imageUrl;
  this.orderDateTime = orderDateTime;
  this.orderTime = orderTime;
  this.orderName = orderName;
  this.orderPrice = orderPrice;
  this.orderQuantity = orderQuantity;
    this.orderStatus = orderStatus;
    this.orderWeight = orderWeight;
 }

    public String getOrderWeight() {
    return orderWeight;
    }
    public void setOrderWeight(String orderWeight) {
    this.orderWeight = orderWeight;
    }

 // Getters
 public String getOrderId() { return orderId; }
 public String getDocumentId() { return documentId; }
 public String getUserId() { return userId; }
 public String getUsername() { return username; }
 public String getAddress() { return address; }
 public String getMobileNumber() { return mobilenumber; }

 public String getImageUrl() { return imageUrl; }
 public String getOrderDateTime() { return orderDateTime; }
 public String getOrderTime() { return orderTime; }
 public String getOrderName() { return orderName; }
 public int getOrderPrice() { return orderPrice; }
 public int getOrderQuantity() { return orderQuantity; }
 public String getOrderStatus() {
  return orderStatus;
 }
 public void setOrderStatus(String orderStatus) {
  this.orderStatus = orderStatus;
 }



}
