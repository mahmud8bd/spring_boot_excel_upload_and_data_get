package com.saz.demo.service;

import com.saz.demo.dto.ItemResponseDto;
import com.saz.demo.util.UploadUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class UploadService {

    private final UploadUtil uploadUtil;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UploadService(UploadUtil uploadUtil, JdbcTemplate jdbcTemplate) {
        this.uploadUtil = uploadUtil;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ItemResponseDto> upload(MultipartFile file) throws Exception {
        Path tempDir = Files.createTempDirectory("");
        File tempFile = tempDir.resolve(Objects.requireNonNull(file.getOriginalFilename())).toFile();
        file.transferTo(tempFile);
        Workbook workbook = WorkbookFactory.create(tempFile);
        Sheet sheet = workbook.getSheetAt(0);
        Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);
        Row headerRow = rowStreamSupplier.get().findFirst().orElseThrow(() -> new IllegalArgumentException("No header row found"));
        List<String> headerCells = uploadUtil.getStream(headerRow)
                .map(Cell::getStringCellValue)
                .map(String::valueOf)
                .toList();

        // Check if the required columns exist in the header
        List<String> requiredColumns = Arrays.asList("Branch Name", "Store Name", "Item Name", "UOM", "OB Qty", "OB Rate");
        if (!new HashSet<>(headerCells).containsAll(requiredColumns)) {
            throw new IllegalArgumentException("Required columns are missing in the header");
        }

        int colCount = headerCells.size();
        List<ItemResponseDto> result = new ArrayList<>();
        Set<String> encounteredItemNames = new HashSet<>(); // Set to store encountered item names
        rowStreamSupplier.get().skip(1).forEach(row -> {
            List<Object> cellList = uploadUtil.getStream(row)
                    .map(this::getCellObject)
                    .toList();

            if (cellList.size() != colCount) {
                return;
            }

            ItemResponseDto itemResponseDto = new ItemResponseDto();
            for (int i = 0; i < colCount; i++) {
                String header = headerCells.get(i);
                Object value = cellList.get(i);
                switch (header) {
                    case "Branch Name":
                        String branchName = (String) value;
                        Long branchId = getBranchIdByName(branchName);
                        itemResponseDto.setBranchId(branchId);
                        itemResponseDto.setBranchName(branchName);
                        break;
                    case "Store Name":
                        String storeName = (String) value;
                        Long storeId = getStoreIdByName(storeName);
                        itemResponseDto.setStoreId(storeId);
                        itemResponseDto.setStoreName(storeName);
                        break;
                    case "Item Name":
                        String itemName = (String) value;
                        if (!encounteredItemNames.add(itemName)) {
                            // Handle duplicate item name
                            // For example:
                            // log.error("Duplicate item name found: {}", itemName);
                            // return;
                        }
                        Long itemId = getItemIdByName(itemName);
                        itemResponseDto.setItemId(itemId);
                        itemResponseDto.setItemName(itemName);
                        break;
                    case "UOM":
                        String uomCode = (String) value;
                        Long uomId = getUomIdByShortCode(uomCode);
                        itemResponseDto.setUomId(uomId);
                        itemResponseDto.setUomName(uomCode);
                        break;
                    case "OB Qty":
                        itemResponseDto.setQty(((Number) value).doubleValue());
                        break;
                    case "OB Rate":
                        itemResponseDto.setRate(((Number) value).doubleValue());
                        break;
                    default:
                        // Handle other columns if needed
                }
            }
            result.add(itemResponseDto);
        });
        return result;
    }

    private Object getCellObject(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            default:
                return null;
        }
    }

    private Long getBranchIdByName(String branchName) {
        return getIdByColumnName("[3B1_company_branch_unit]", "b_u_name", branchName);
    }

    private Long getStoreIdByName(String storeName) {
        return getIdByColumnName("[3C1_company_store_location]", "sl_name", storeName);
    }

    private Long getItemIdByName(String itemName) {
        return getIdByColumnName("[3N05_item_info]", "display_itm_name", itemName);
    }

    private Long getUomIdByShortCode(String shortCode) {
        return getIdByColumnName("[2F2_sv_uom]", "uom_short_code", shortCode);
    }

    private Long getIdByColumnName(String tableName, String columnName, String value) {
        try {
            String sql = "SELECT id FROM " + tableName + " WHERE " + columnName + " = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, value);
        } catch (EmptyResultDataAccessException e) {
            return null; // or handle as needed
        }
    }
}
