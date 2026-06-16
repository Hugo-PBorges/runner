package com.ufg.runner.assinador.service;

import com.ufg.runner.assinador.config.Pkcs11Properties;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeBuilder;
import com.ufg.runner.assinador.dto.outcome.OperationOutcomeDTO;
import com.ufg.runner.assinador.dto.sign.CryptoDTO;
import com.ufg.runner.assinador.dto.sign.SignRequestDTO;
import com.ufg.runner.assinador.dto.validate.ValidateRequestDTO;
import com.ufg.runner.assinador.exception.BusinessValidationException;
import com.ufg.runner.assinador.exception.CryptographicException;
import com.ufg.runner.assinador.validator.SignValidator;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public class Pkcs11SignatureService implements SignatureService {

    private final SignValidator signValidator;
    private final Pkcs11Properties pkcs11Properties;

    public Pkcs11SignatureService(SignValidator signValidator, Pkcs11Properties pkcs11Properties) {
        this.signValidator = signValidator;
        this.pkcs11Properties = pkcs11Properties;
    }

    @Override
    public OperationOutcomeDTO sign(SignRequestDTO request) {

        signValidator.validate(request);

        CryptoDTO crypto = request.getCrypto();

        Provider provider;
        try {
            String libraryPath = pkcs11Properties.getLibraryPath().replace("\\", "/");
            String config = "--name=SoftHSM\nlibrary = " + libraryPath + "\nslotListIndex = 0";

            Provider base = Security.getProvider("SunPKCS11");
            provider = base.configure(config);
            String providerName = provider.getName();
            if (Security.getProvider(providerName) == null) {
                Security.addProvider(provider);
            } else {
                provider = Security.getProvider(providerName);
            }
        } catch (InvalidParameterException | ProviderException e) {
            throw new CryptographicException("Erro ao acessar dispositivo criptográfico: " + e.getMessage(), e);
        }

        try {
            KeyStore ks = KeyStore.getInstance("PKCS11", provider);
            ks.load(null, crypto.getPin().toCharArray());

            PrivateKey privateKey = (PrivateKey) ks.getKey(crypto.getIdentifier(), crypto.getPin().toCharArray());

            if (privateKey == null) {
                java.util.List<String> aliases = java.util.Collections.list(ks.aliases());
                throw new CryptographicException(
                        "Chave privada não encontrada: '" + crypto.getIdentifier()
                        + "'. Aliases disponíveis: " + aliases
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

            return OperationOutcomeBuilder.information("SIGNATURE_CREATED", jws);

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException
                 | java.security.cert.CertificateException | java.io.IOException
                 | InvalidKeyException | SignatureException e) {
            throw new CryptographicException("Erro ao acessar dispositivo criptográfico: " + e.getMessage(), e);
        }
    }

    @Override
    public OperationOutcomeDTO validate(ValidateRequestDTO request) {
        try {
            Base64.getDecoder().decode(request.getBase64());
            return OperationOutcomeBuilder.information("SIGNATURE_VALID", "Assinatura válida com sucesso");
        } catch (IllegalArgumentException e) {
            throw new BusinessValidationException("VALIDATION.INVALID-BASE64", "Base64 inválido");
        }
    }
}
