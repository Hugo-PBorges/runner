package com.ufg.runner.assinador.services;

import com.ufg.runner.assinador.dto.outcome.IssueDTO;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.outcome.builder.OperationOutcomeBuilder;
import com.ufg.runner.assinador.dto.sign.CryptoDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.validator.SignValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.List;
import java.util.Set;

//@Service
public class PKCS11SignatureService implements SignatureService {

    private final Validator validator;
    private final SignValidator signValidator;

    public PKCS11SignatureService(Validator validator, SignValidator signValidator) {
        this.validator = validator;
        this.signValidator = signValidator;
    }

    @Override
    public OperationOutcomeDTO sign(SignRequestDTO request) {
        Set<ConstraintViolation<SignRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            ConstraintViolation<?> v = violations.iterator().next();
            return OperationOutcomeBuilder.error(
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }

        OperationOutcomeDTO businessError = signValidator.validate(request);
        if (businessError != null) {
            return businessError;
        }

        CryptoDTO crypto = request.getCrypto();

        try {
            String config = String.format(
                    "--name SunPKCS11\nlibrary /usr/lib/softhsm/libsofthsm2.so\nslot %d",
                    crypto.getSlotId()
            );

            Provider provider = Security.getProvider("SunPKCS11")
                    .configure(config);
            Security.addProvider(provider);

            KeyStore ks = KeyStore.getInstance("PKCS11", provider);
            ks.load(null, crypto.getPin().toCharArray());

            PrivateKey privateKey = (PrivateKey) ks.getKey(
                    crypto.getIdentifier(), null
            );

            if (privateKey == null) {
                return OperationOutcomeBuilder.error(
                        "Chave privada não encontrada para o identifier: " + crypto.getIdentifier()
                );
            }

            String payload = "{\"bundleSize\":\"" + request.getBundle().getEntries().size() + "\"}";

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(payload.getBytes(StandardCharsets.UTF_8));
            byte[] signed = sig.sign();

            String header = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("{\"alg\":\"RS256\"}".getBytes(StandardCharsets.UTF_8));
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            String signature = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(signed);

            String jws = header + "." + encodedPayload + "." + signature;

            IssueDTO issue = new IssueDTO();
            issue.setSeverity("information");
            issue.setCode("SIGNATURE_CREATED");
            issue.setDiagnostics(jws);

            OperationOutcomeDTO outcome = new OperationOutcomeDTO();
            outcome.setIssue(List.of(issue));

            return outcome;

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException
                 | CertificateException | IOException
                 | InvalidKeyException | SignatureException e) {
            return OperationOutcomeBuilder.error(
                    "Erro ao acessar dispositivo criptográfico: " + e.getMessage()
            );
        }
    }

    @Override
    public OperationOutcomeDTO validate(ValidateRequestDTO request) {
        Set<ConstraintViolation<ValidateRequestDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            ConstraintViolation<?> v = violations.iterator().next();
            return OperationOutcomeBuilder.error(
                    v.getPropertyPath() + " " + v.getMessage()
            );
        }

        try {
            Base64.getDecoder().decode(request.getBase64());

            IssueDTO issue = new IssueDTO();
            issue.setSeverity("information");
            issue.setCode("SIGNATURE_VALID");
            issue.setDiagnostics("Assinatura válida com sucesso");

            OperationOutcomeDTO outcome = new OperationOutcomeDTO();
            outcome.setIssue(List.of(issue));

            return outcome;

        } catch (IllegalArgumentException e) {
            return OperationOutcomeBuilder.error("Base64 inválido");
        }
    }
}