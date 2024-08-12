package com.emprendev.controller;

import com.emprendev.entity.Offer;
import com.emprendev.exceptions.ResourceNotFoundException;
import com.emprendev.services.OfferServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "http://localhost") // Permitir CORS si es necesario
public class  OfferController {

    @Autowired
    private OfferServices offerService;

    @PostMapping
    public ResponseEntity<Offer> createOffer(
            @RequestPart("title") String title,
            @RequestPart("description") String description,
            @RequestPart("payment") String payment,
            @RequestPart("fields") String fields,
            @RequestPart(value = "imageUrl1", required = false) MultipartFile imageUrl1,
            @RequestPart(value = "imageUrl2", required = false) MultipartFile imageUrl2,
            @RequestPart(value = "imageUrl3", required = false) MultipartFile imageUrl3,
            @RequestPart(value = "imageUrl4", required = false) MultipartFile imageUrl4
    ) {
        Offer offer = new Offer();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setPayment(Long.parseLong(payment));
        offer.setFields(Integer.parseInt(fields));

        // Establecer valores predeterminados para fechas y estado
        offer.setCreationDate(String.valueOf(LocalDate.now()));
        offer.setFinalizationDate(String.valueOf(LocalDate.now().plusMonths(1)));
        offer.setOfferState(1);

        // Aquí puedes manejar la lógica para guardar los archivos de imagen si es necesario.

        Offer savedOffer = offerService.saveOffer(offer);
        return ResponseEntity.ok(savedOffer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long id) {
        Optional<Offer> offer = offerService.getOfferById(id);
        if (offer.isPresent()) {
            return ResponseEntity.ok(offer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Offer> updateOffer(@PathVariable Long id, @RequestBody Offer offerDetails) {
        try {
            System.out.println("Updating offer with id: " + id);
            System.out.println("Offer details: " + offerDetails);
            Offer updatedOffer = offerService.updateOffer(id, offerDetails);
            return ResponseEntity.ok(updatedOffer);
        } catch (ResourceNotFoundException e) {
            System.out.println("Offer not found with id: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}
