<template>
<div>


</div>
  
</template>

<script>
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
        initMap(){
            this.map = new Tmap.Map("map_div", {
			center : new Tmap.LatLng(37.570028, 126.986072),
			width : "100%",
			height : "400px",
			zoom : 15,
			zoomControl : true,
			scrollwheel : true


    })
        }

    },
    mounted(){
        if (window.Tmapv2 && window.Tmapv2.Map) {
            this.map = new Tmapv2.Map("map_div", {
			center : new Tmapv2.LatLng(37.570028, 126.986072),
			width : "100%",
			height : "400px",
			zoom : 15,
			zoomControl : true,
			scrollwheel : true


    })
        }else {
        const script = document.createElement('script');
        
        script.onload = () => Tmap.Map.load(this.initMap);
        script.src = "https://apis.openapi.sk.com/tmap/jsv2?version=1&appKey=l7xx85f17a9a757349b59a2d9eb9d1382cb3";
        document.head.appendChild(script);
      }
        
    }


}
</script>

<style>

</style>