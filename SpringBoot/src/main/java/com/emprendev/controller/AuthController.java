package com.emprendev.controller;

import com.emprendev.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/request_password_reset")
    public ResponseEntity<?> requestRecover(@RequestParam String email) {
        if (email != null) {
            // Lógica para enviar el correo de restablecimiento
            try {
                userServices.sendRecoverCode(email);
                return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Correo de restablecimiento enviado\"}");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"Error al enviar el correo de restablecimiento: " + e.getMessage() + "\"}");
            }
        } else {
            return ResponseEntity.badRequest().body("El parámetro 'correo' es requerido");
        }
    }

    @PostMapping("/reset_password")
    public ResponseEntity<?> changePassword(@RequestParam String token, @RequestParam String newPassword) {
        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Los parámetros 'token' y 'nuevaContrasena' son requeridos");
        }

        // Lógica para cambiar la contraseña
        try {
            userServices.changePassword(token, newPassword);
            return ResponseEntity.ok().body("{\"status\": \"success\", \"message\": \"Contraseña restablecida exitosamente\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"Error al restablecer la contraseña: " + e.getMessage() + "\"}");
        }
    }
}
