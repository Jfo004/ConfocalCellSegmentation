/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.time.Instant;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author janlu
 */
public class InstantAdapter extends XmlAdapter<String,Instant>{
    @Override
    public Instant unmarshal(String text) throws Exception {
        return Instant.parse(text);
    }
    
    @Override
    public String marshal(Instant instant) {
        return instant.toString();
    }
}
