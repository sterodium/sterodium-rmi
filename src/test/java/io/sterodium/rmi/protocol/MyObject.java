package io.sterodium.rmi.protocol;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 16.11.2015
 */
public class MyObject {

    public String publicMethod() {
        return "OK";
    }

    public String exceptionThrowingMethod() {
        throw new RuntimeException("RMI protocol should handle me");
    }

    protected String protectedMethod() {
        return "I am invisible for RMI protocol";
    }

    public String acceptLongParameter(long parameter) {
        return "OK";
    }

    public boolean getBoolean(boolean arg) {
        return arg;
    }

    public byte getByte(byte arg) {
        return arg;
    }

    public char getChar(char arg) {
        return arg;
    }

    public short getShort(short arg) {
        return arg;
    }

    public int getInt(int arg) {
        return arg;
    }

    public long getLong(long arg) {
        return arg;
    }

    public float getFloat(float arg) {
        return arg;
    }

    public double getDouble(double arg) {
        return arg;
    }


    public Boolean getBigBoolean(Boolean arg) {
        return arg;
    }

    public Byte getBigByte(Byte arg) {
        return arg;
    }

    public Character getBigCharacter(Character arg) {
        return arg;
    }

    public Short getBigShort(Short arg) {
        return arg;
    }

    public Integer getBigInteger(Integer arg) {
        return arg;
    }

    public Long getBigLong(Long arg) {
        return arg;
    }

    public Float getBigFloat(Float arg) {
        return arg;
    }

    public Double getBigDouble(Double arg) {
        return arg;
    }

    public String getString(String arg) {
        return arg;
    }

}
