package com.example.test.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;

@SuppressWarnings("serial")
@Tag("iframe")
public class IframeMovie extends Component implements HasSize {

    public IframeMovie(){

    }

    public IframeMovie(String src){

        setHeight("500px");
        setWidth("800px");
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
