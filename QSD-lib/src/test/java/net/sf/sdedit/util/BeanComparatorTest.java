package net.sf.sdedit.util;

import java.util.Random;
import java.util.TreeSet;

public class BeanComparatorTest {

    public static class TestBean {
        
        private int num1;
        
        private int num2;
        
        public TestBean(int num1, int num2) {
            this.num1 = num1;
            this.num2 = num2;
        }

        public void setNum1(int num1) {
            this.num1 = num1;
        }

        public int getNum1() {
            return num1;
        }

        public void setNum2(int num2) {
            this.num2 = num2;
        }

        public int getNum2() {
            return num2;
        }
        
        public String toString () {
            return num1 + "/" + num2;
        }
        
        
        
    }
    
    public static void main (String [] argv) {
        Random rnd = new Random();
        BeanComparator<TestBean> bc = new BeanComparator<TestBean>(TestBean.class,BeanComparator.COMPARABLE,"num2","num1");
        TreeSet<TestBean> set = new TreeSet<TestBean>(bc);
        set.add(new TestBean(rnd.nextInt(10000),9));
        for (int i = 0; i < 20; i++) {
            set.add(new TestBean(rnd.nextInt(10000),rnd.nextInt(10000)));
        }
        System.out.println(set);
    }
	
}
