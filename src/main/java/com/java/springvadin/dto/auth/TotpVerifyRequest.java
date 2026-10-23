package com.java.springvadin.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record TotpVerifyRequest(
         String tempToken,
         String code
) {
}
