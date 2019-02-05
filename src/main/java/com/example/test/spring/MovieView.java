package com.example.test.spring;

import com.example.test.html.IframeMovie;
import com.example.test.html.Movie;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;


import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;



import java.util.Arrays;
import java.util.List;


public class MovieView extends Div {
    List<Movie> movies = Arrays.asList(
            new Movie("370128881944360988797035","forrest gump","1994","us"),
            new Movie("969695915480097163410841","basic instinct","1999","us"),
            new Movie("952304731029494039791478","graduate","2000","us"),
            new Movie("396837209789871026056215","like sunday like rain","2001","us"),
            new Movie("434646452543223252281583","schindler list","2003","us"),
            new Movie("741643012217206953423822","romeo and juliet","2004","us"),
            new Movie("855147463535581819521501","taxi driver","2010","us"),
            new Movie("345552493306474938277741","crossing over","2010","us"),
            new Movie("688506699885964985701859","chloe","2010","us")
    );
    String url1 = "http://myantmedia.ddns.net:5080/LiveApp/play.html?name=";
    String url = url1 + "969695915480097163410841";
     // String url = "http://admin:12345abcde@192.168.1.38/Streaming/channels/1/httppreview";
    String info;
    IframeMovie video1 = new IframeMovie(url);
    @Autowired
    MessageBean bean;

   // public void chooseMovie(int ,IframeMovie video){

   //     String title = movies.get(number).getTitle();
   //     Notification.show("Welcome Watching "+title);
   //     url = url1+movies.get(number).getStreamId();
   //     video.setSrc(url);



    //}


    public MovieView() {



        Button information = new Button(info);







        Div movieLayout = new Div();
        Div selectLayout = new Div();





        movieLayout.add(video1);



        Grid<Movie> grid = new Grid<>();
        grid.setItems(movies);
        grid.addColumn(Movie::getTitle).setHeader("Title");
        grid.addColumn(Movie::getCountry).setHeader("Country");
        grid.addColumn(Movie::getYear).setHeader("Year");

        //grid.setSelectionMode(SelectionMode.NONE);
        grid.addItemClickListener(event -> {System.out
                .println(("Clicked Item: " + event.getItem().getStreamId()));
            Notification.show("Welcome Watching "+event.getItem().getTitle());
            url = url1+event.getItem().getStreamId();
            video1.setSrc(url);

        });

        selectLayout.setWidth("800px");
        selectLayout.add(grid);

        getElement().setAttribute("align","center");
        add(movieLayout,selectLayout);

    }

}
