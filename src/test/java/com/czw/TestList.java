package com.czw;

import com.czw.dto.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: ChengZiwang
 * @date: 2020/7/28
 **/
public class TestList {

    @Test
    void test000(){
        String afpIds="1,2,3,5,7";
        List<String> result = Arrays.asList(afpIds.split(","));
        StringBuffer afpId = new StringBuffer();
        for (int i = 0; i < result.size(); i++) {
            afpId.append(result.get(i));
            if (i != afpId.length() - 1) {
                afpId.append(",");
            }

        }
        afpId.deleteCharAt(afpId.length() - 1);
        System.out.println(afpId);
    }
}
class TestTTT{
    Object object;

    @Override
    public String toString() {
        return "Test{" +
                "object=" + object +
                '}';
    }

    public static void main(String[] args) {
        TestTTT test = new TestTTT();
        test.object=new ArrayList<>();
        System.out.println(test);

    }
}