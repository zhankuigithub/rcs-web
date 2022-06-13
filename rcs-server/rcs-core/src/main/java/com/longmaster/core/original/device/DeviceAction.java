package com.longmaster.core.original.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceAction {

    private RequestDeviceSpecifics requestDeviceSpecifics;

}
