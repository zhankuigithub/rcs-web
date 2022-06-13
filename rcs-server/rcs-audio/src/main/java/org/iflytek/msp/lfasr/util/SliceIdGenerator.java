package org.iflytek.msp.lfasr.util;

public class SliceIdGenerator {

    private static final String INIT_STR = "aaaaaaaaa`";
    private static final int LENGTH = "aaaaaaaaa`".length();
    private char[] ch = "aaaaaaaaa`".toCharArray();

    public SliceIdGenerator() {
    }

    public String getNextSliceId() {
        for(int j = LENGTH - 1; j >= 0; --j) {
            if (this.ch[j] != 'z') {
                ++this.ch[j];
                break;
            }

            this.ch[j] = 'a';
        }

        return new String(this.ch);
    }
}
