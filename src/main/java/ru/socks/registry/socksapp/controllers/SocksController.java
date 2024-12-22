package ru.socks.registry.socksapp.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.socks.registry.socksapp.exceptions.FileProcessingException;
import ru.socks.registry.socksapp.exceptions.InvalidDataFormatException;
import ru.socks.registry.socksapp.models.Socks;
import ru.socks.registry.socksapp.services.SocksService;
import ru.socks.registry.socksapp.utils.QuantityFilterType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/socks")
public class SocksController {

    private final SocksService socksService;

    @PostMapping("/batch")
    @Operation(summary = "Upload CSV file", description = "Uploads a CSV file containing socks data")
    public ResponseEntity<?> uploadSocks(
            @RequestParam("file")
            @Parameter(description = "CSV file to upload", required = true) MultipartFile file) {
        List<Socks> socksList = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream())){
            CSVFormat csvFormat = CSVFormat.Builder.create()
                    .setHeader("color", "quantity", "cottonPart")
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                try {
                    Socks socks = new Socks();
                    socks.setColor(record.get("color"));
                    socks.setQuantity(Integer.parseInt(record.get("quantity")));
                    socks.setCottonPart(Integer.parseInt(record.get("cottonPart")));
                    socksList.add(socks);
                } catch (InvalidDataFormatException e) {
                    log.error("Invalid data in record {}", record, e);
                } catch (Exception e) {
                    log.error("Unexpected error in parsing record {}", record, e);
                    throw new FileProcessingException("Error processing record");
                }
            }

            socksService.saveAll(socksList);

            return ResponseEntity.status(HttpStatus.OK).body(socksList);
        } catch (IOException e) {
            log.error("Error parsing CSV file {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error processing file " + file.getOriginalFilename());
        } catch (FileProcessingException e) {
            log.error("Error processing file {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Error processing file " + file.getOriginalFilename());
        }
    }

    @Operation(summary = "Get all Socks", description = "Returns list of all Socks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    })
    @GetMapping
    public ResponseEntity<List<Socks>> getAllSocks(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String filterType
            ) {
        log.info("Get list of all socks with filters");

        QuantityFilterType filter = null;
        if (filterType != null) {
            try {
                filter = QuantityFilterType.valueOf(filterType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        List<Socks> filteredSocks = socksService.getFilteredSocks(color, quantity, filter);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredSocks);
    }

    @Operation(summary = "Income Socks", description = "Creates a new Socks entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created"),
    })
    @PostMapping("/income")
    public ResponseEntity<Socks> addSocks(@RequestBody Socks socks) {
        log.info("Add socks - income. New socks added");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(socksService.createSocks(socks));
    }

    @Operation(summary = "Delete Socks by id", description = "Deletes Socks as per id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not Found - Nothing to delete")
    })
    @DeleteMapping("/outcome/{id}")
    public ResponseEntity<Socks> deleteSocks(@PathVariable("id") Long id) {
        log.info("Delete socks - outcome socks with id = {}", id);

        socksService.deleteSocksById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "Update existing Socks by id", description = "Updates existing Socks as per id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "404", description = "Not Found - Nothing to update")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Socks> updateSocks(@PathVariable("id") Long id, @RequestBody Socks socks) {
        log.info("Update socks - change any value of socks with id = {}", id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(socksService.updateSocks(id, socks));
    }
}
