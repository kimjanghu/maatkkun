<template>
<div>
 <div id="map_div">
        </div>    


</div>
  
</template>

<script>
// import axios from 'axios'
export default {
    name:"Recommend",
    data(){
        return {
            map:'',
            marker_s:'',
            marker_e:'',
            marker_p1:'',
            marker_p2:'',
            totalMarkerArr:[],
            drawInfoArr:[],
            resultdrawArr:[],


        }
    },
    methods:{
        initTmap(){
           this.map = new Tmapv2.Map('map_div',{
               center : new Tmapv2.LatLng(37.570028, 126.986072),
               width:'100%', 
               height:'400px',
               zoom : 15,
			zoomControl : true,
			scrollwheel : true});
        
        this.marker_s = new Tmapv2.Marker(
				{
					position : new Tmapv2.LatLng(37.569758, 126.977022),
					icon : "http://v2apis.sktelecom.com/upload/tmap/marker/pin_r_m_s.png",
					iconSize : new Tmapv2.Size(24, 38),
					map : this.map
                });
        this.marker_e = new Tmapv2.Marker(
				{
					position : new Tmapv2.LatLng(37.570594, 126.997589),
					icon : "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_r_m_e.png",
					iconSize : new Tmapv2.Size(24, 38),
					map : this.map
                });
        this.marker_p1 = new Tmapv2.Marker(
				{
					position : new Tmapv2.LatLng(37.573028, 126.983072),
					icon : "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
					iconSize : new Tmapv2.Size(24, 38),
					map : this.map
                });
        this.marker_p2 = new Tmapv2.Marker(
				{
					position : new Tmapv2.LatLng(37.565778, 126.987319),
					icon : "http://tmapapis.sktelecom.com/upload/tmap/marker/pin_b_m_p.png",
					iconSize : new Tmapv2.Size(24, 38),
					map : this.map
				});


    }
        }

    ,
    mounted(){
        if (window.Tmapv2 && window.Tmapv2.Map) {
        this.initTmap();
      } else {
        const script = document.createElement('script');
        /* global Tmapv2 */
        script.onload = () => Tmapv2.Map.load(this.initTmap);
        script.src = "https://apis.openapi.sk.com/tmap/js?version=1&format=javascript&appKey=l7xx85f17a9a757349b59a2d9eb9d1382cb3";
        document.head.appendChild(script);
      }
        
        
    },
    updated(){
        
    }


}
</script>

<style>

</style>