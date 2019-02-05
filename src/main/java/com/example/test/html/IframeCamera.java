package com.example.test.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;

@SuppressWarnings("serial")
@Tag("iframe")
public class IframeCamera extends Component implements HasSize {

    public IframeCamera(){

    }

    public IframeCamera(String src){

        setHeight("4800px");
        setWidth("640px");
        setSrc(src);


    }

    public void setSrc(String src){

        getElement().setProperty("src",src);
        getElement().setAttribute("allowfullscreen",true);
        getElement().setAttribute("frameborder","0");
          //  getElement().getStyle().set("height","100%");
          //  getElement().getStyle().set("width","100%");
          //  getElement().getStyle().set("resize","both");
          //  getElement().getStyle().set("object-fit","fill");
        getElement().getStyle().set("align","center");
    }

}
