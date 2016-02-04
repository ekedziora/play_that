function initMap() {
    var map = new google.maps.Map(document.getElementById('map'));

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function (position) {
                map.setCenter({
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                });
                map.setZoom(15);
            },
            function () {
                map.setCenter({
                    lat: 72,
                    lng: -40
                });
                map.setZoom(3);
            }
        );
    }

    var input = document.getElementById("addressAutocomplete");

    var autocomplete = new google.maps.places.Autocomplete(input);
    autocomplete.bindTo('bounds', map);

    var infowindow = new google.maps.InfoWindow();
    var marker = new google.maps.Marker({
        map: map,
        anchorPoint: new google.maps.Point(0, -30)
    });

    google.maps.event.addListener(map, 'click', function(event) {
        marker.setPosition(event.latLng);

        var url = 'http://maps.googleapis.com/maps/api/geocode/json';
        var latlng = {latlng: event.latLng.lat() + ',' + event.latLng.lng()};

        $.getJSON(url, latlng, function(data) {
            if (data.results.length > 0) {
                var foundAddress = data.results[0].formatted_address;
                $('#addressAutocomplete').val(foundAddress);
                $('#lat').val(event.latLng.lat());
                $('#lng').val(event.latLng.lng());
                infowindow.setContent(foundAddress);
                infowindow.open(map, marker);
            } else {
                $('#addressAutocomplete').val(null);
                $('#lat').val(null);
                $('#lng').val(null);
                infowindow.setContent(null);
            }
        });
    });

    google.maps.event.addListener(marker, 'click', function() {
        infowindow.open(map, marker);
    });

    var place;

    autocomplete.addListener('place_changed', function () {
        infowindow.close();
        marker.setVisible(false);
        place = autocomplete.getPlace();
        if (!place.geometry) {
            return;
        }

        if (place.geometry.viewport) {
            map.fitBounds(place.geometry.viewport);
        } else {
            map.setCenter(place.geometry.location);
            map.setZoom(15);
        }
        marker.setIcon({
            url: place.icon,
            size: new google.maps.Size(71, 71),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(17, 34),
            scaledSize: new google.maps.Size(35, 35)
        });
        marker.setPosition(place.geometry.location);
        marker.setVisible(true);

        infowindow.setContent('<div><strong>' + place.name + '</strong><br>' + place.adr_address);

        $("#lat").val(place.geometry.location.lat());
        $("#lng").val(place.geometry.location.lng());
    });

    input.addEventListener('change', function() {
        $("#lat").val(null);
        $("#lng").val(null);
    });

    $('#mapModal').on('shown.bs.modal', function () {
        google.maps.event.trigger(map, "resize");
        map.setCenter(marker.getPosition());
        if (place) {
            infowindow.open(map, marker);
        }
    });
}