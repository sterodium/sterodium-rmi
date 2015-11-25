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





    public boolean[] getBooleanArray(boolean[] arg) {
        return arg;
    }

    public byte[] getByteArray(byte[] arg) {
        return arg;
    }

    public char[] getCharArray(char[] arg) {
        return arg;
    }

    public short[] getShortArray(short[] arg) {
        return arg;
    }

    public int[] getIntArray(int[] arg) {
        return arg;
    }

    public long[] getLongArray(long[] arg) {
        return arg;
    }

    public float[] getFloatArray(float[] arg) {
        return arg;
    }

    public double[] getDoubleArray(double[] arg) {
        return arg;
    }


    public Boolean[] getBigBooleanArray(Boolean[] arg) {
        return arg;
    }

    public Byte[] getBigByteArray(Byte[] arg) {
        return arg;
    }

    public Character[] getBigCharacterArray(Character[] arg) {
        return arg;
    }

    public Short[] getBigShortArray(Short[] arg) {
        return arg;
    }

    public Integer[] getBigIntegerArray(Integer[] arg) {
        return arg;
    }

    public Long[] getBigLongArray(Long[] arg) {
        return arg;
    }

    public Float[] getBigFloatArray(Float[] arg) {
        return arg;
    }

    public Double[] getBigDoubleArray(Double[] arg) {
        return arg;
    }

    public String[] getStringArray(String[] arg) {
        return arg;
    }

}
