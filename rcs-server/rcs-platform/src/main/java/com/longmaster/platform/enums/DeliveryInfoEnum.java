package com.longmaster.platform.enums;

//sent：消息已发送
//failed：消息发送失败
//delivered：消息已送达
//displayed：消息已阅读
//deliveredToNetwork：已转短信发送
//revokeOk：消息撤回成功
//revokeFail：消息撤回失败 1 已发送 2发送失败 3 消息已送达 4 已阅读 5 已转短 6已撤回
public enum DeliveryInfoEnum {

    SENT("sent", 1),
    FAILED("failed", 2),
    DELIVERY("delivered", 3),
    DISPLAYED("displayed", 4),
    DELIVERED_TO_NETWORK("deliveredToNetwork", 5),
    REVOKER_OK("revokeOk", 6),
    REVOKER_FAIL("revokeFail", 7),


    MESSAGE_SENT("MessageSent", 1),
    DELIVERY_IMPOSSIBLE("DeliveryImpossible", 2),
    DELIVERED_TO_TERMINAL("DeliveredToTerminal", 3),
    MESSAGE_DISPLAYED("MessageDisplayed", 4);

    // MessageSent 消息已发送到平台   DeliveredToTerminal  RCS消息达到终端  DeliveryImpossible  发送失败   MessageDisplayed 消息已阅

    private String mode;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    DeliveryInfoEnum(String mode, int type) {
        this.mode = mode;
        this.type = type;
    }

    public static Integer getTypeByMode(String mode) {
        DeliveryInfoEnum[] modeEnums = DeliveryInfoEnum.values();
        for (DeliveryInfoEnum modeEnum : modeEnums) {
            if (modeEnum.getMode().equalsIgnoreCase(mode)) {
                return modeEnum.getType();
            }

        }
        return null;
    }
}
