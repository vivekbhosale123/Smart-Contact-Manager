console.log("This is a JS page");

const toggleSidebar = () => {
    // Use fadeToggle for smooth hide/show animation
    $(".sidebar").fadeToggle(300); // 300ms for animation

    // Adjust the content margin depending on sidebar visibility
    if ($(".sidebar").is(":visible")) {
        $(".content").css("margin-left", "20%");
    } else {
        $(".content").css("margin-left", "0%");
    }
    
    console.log("Sidebar toggled");
};

const search=()=>{
    
    let query=$("#search-input").val();

    if(query=="")
    {
        $(".search-result").hide();
    }else{
        // search
        console.log(query);

        // sending request to server
        let url=`http://localhost:1999/search/${query}`;

        fetch(url).then((response)=>{
             return response.json();
        }).then((data)=>{
            // data
           // console.log(data);

            let text=`<div class='list-group'>`


            data.forEach((contact)=>{
                text+=`<a href='/user/${contact.cid}/contact' class='list-group-item list-group-item-action'>${contact.name}</a>`
            });


            text+=`</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
        });

      
    }
}
