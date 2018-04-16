package com.divya.mettl.first.phase.util;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
public abstract class Constants {

    public Constants() {
    }

    public static enum ProgramClass {
        GOLD_CLASS(1), SILVER_CLASS(2), NORMAL_CLASS(3);

        private int value;

        private ProgramClass(int value) {
            this.value = value;
        }

        public int getVal() {
            return value;
        }

        public static ProgramClass getProgramClassFromVal(int val) {

            switch (val) {
                case 1:
                    return GOLD_CLASS;
                case 2:
                    return SILVER_CLASS;
                case 3:
                    return NORMAL_CLASS;
                default:
                    return null;
            }
        }
    }

}
