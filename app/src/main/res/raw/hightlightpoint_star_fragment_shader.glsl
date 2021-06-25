precision mediump float;
//varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
//uniform lowp float mixCOEF;
//uniform lowp float isOrigin;
//uniform lowp vec4 colorRGBTexture;
void main(){

    gl_FragColor = texture2D(inputImageTexture, gl_PointCoord);
    //highp vec4 abc = texture2D(inputImageTexture, textureCoordinate);
    //    if(isOrigin > 0.5){
    //        gl_FragColor = abc;
    //    }else{
    ////           gl_FragColor = vec4(colorRGBTexture.rgb*abc.a*mixCOEF,abc.a*mixCOEF);
    ////            gl_FragColor = vec4(abc.rgb*abc.a*mixCOEF,abc.a*mixCOEF);
    //            gl_FragColor = vec4(abc.rgb,abc.a*mixCOEF);
    //     }
}