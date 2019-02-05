package com.example.test.html;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

public class LightButton extends Button {
    

    private String name;
    private String insteonId;

    private int left;
    private int top;


    private boolean status = false;

    public String getName() {
        return name;
    }

    public String getInsteonId() {
        return insteonId;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public boolean isStatus() {
        return status;
    }

    public LightButton(String name, String insteonId, int left, int top) {
        this.name = name;
        this.insteonId = insteonId;
        this.left = left;
        this.top = top;

          setWidth("80px");
          setHeight("80px");
         // getElement().getStyle().set("background","transparent");

          if (status)
              setOnImg();
          else
              setOffImg();



    }


   // public LightButton(){

    //    setWidth("80px");
    //    setHeight("80px");
    //    getElement().getStyle().set("background","transparent");
    //    if (status)
    //        setOnImg();
    //    else
    //        setOffImg();
   // }

    public void setOnImg() {

        setIcon(new Image("/frontend/images/lightOn40X40.png", "lightOn"));

    }
    public void setOffImg() {

        setIcon(new Image("/frontend/images/lightOff40X40.png", "lightOff"));
    }

    public  void toggleImage(int value){

        if (value ==0)
            setOnImg();
        else
            setOffImg();


    }

    public  void toggle1Image(int value){

        if (value ==0)
            setOffImg();
        else
            setOnImg();


    }

    public  void toggle(){
            status = !status;
        if (!status)
            setOnImg();
        else
            setOffImg();


    }
}
