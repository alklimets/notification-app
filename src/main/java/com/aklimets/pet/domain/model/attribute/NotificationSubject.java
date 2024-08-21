package com.aklimets.pet.domain.model.attribute;

import com.aklimets.pet.buildingblock.interfaces.DomainAttribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
public class NotificationSubject extends DomainAttribute<String> {

    @NotNull
    private String value;
}
