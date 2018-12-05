package com.scissorhands;

/**
 * Created by RoyChan on 2017/9/17.
 */

interface Formula {
    double calculate(int a);

    default double sqrt(int a) {
        return Math.sqrt(a);
    }
}

interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}

class Person {
    String firstName;
    String lastName;

    Person() {}

    Person(double a){

    }

    static Double aDouble(double a){
       return 1.0d;
    }

}

class Something {
    static String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }
}

public class LambdaTest {
    public static void main(String[] args) {

        int num = 1;

        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {

                return sqrt(a * 100);
            }
        };

//        Formula formula1 = a -> sqrt(a * 100);

//        PersonFactory<Person> personPersonFactory = Person::new;

        Formula f = Person::aDouble;

        formula.calculate(100);     // 100.0
        formula.sqrt(16);           // 4.0
    }
}
