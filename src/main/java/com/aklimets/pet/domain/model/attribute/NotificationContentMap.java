package com.aklimets.pet.domain.model.attribute;

import com.aklimets.pet.buildingblock.interfaces.DomainAttribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = true)
public class NotificationContentMap extends DomainAttribute<Map<String, String>> {

    @NotNull
    private Map<String, String> value;

}
