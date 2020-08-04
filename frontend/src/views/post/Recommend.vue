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
           this.map = new Tmap.Map({
               div:'map_div', 
               width:'100%', 
               height:'630px'});
        this.map.setCenter(new Tmap.LonLat(126.983045, 37.572194).transform("EPSG:4326", "EPSG:3857"), 15);


    }
        }

    ,
    mounted(){
        if (window.Tmap && window.Tmap.Map) {
        this.initTmap();
      } else {
        const script = document.createElement('script');
        /* global Tmap */
        script.onload = () => Tmap.Map.load(this.initTmap);
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