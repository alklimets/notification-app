package com.aklimets.pet.domain.model.attribute;

import com.aklimets.pet.buildingblock.interfaces.DomainAttribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@EqualsAndHashCode
public class NotificationSubject implements DomainAttribute<String> {

    @NotNull
    private String value;
}
