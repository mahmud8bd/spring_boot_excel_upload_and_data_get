package com.saz.demo.dto;

public class ItemResponseDto {
    private Long branchId;
    private String branchName;
    private Long storeId;
    private String storeName;
    private Long itemId;
    private String itemName;
    private Long uomId;

    private String uomName;
    private Double qty;
    private Double rate;

    public ItemResponseDto() {
    }

    public ItemResponseDto(Long branchId, String branchName, Long storeId, String storeName, Long itemId, String itemName, Long uomId, String uomName, Double qty, Double rate) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.storeId = storeId;
        this.storeName = storeName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.uomId = uomId;
        this.uomName = uomName;
        this.qty = qty;
        this.rate = rate;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getUomId() {
        return uomId;
    }

    public void setUomId(Long uomId) {
        this.uomId = uomId;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
