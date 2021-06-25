varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform lowp float mixCOEF;
uniform lowp float isOrigin;
uniform lowp float isRenderColor;
uniform lowp vec4 colorRGBTexture;
void main(){
    highp vec4 abc = texture2D(inputImageTexture, textureCoordinate);
    if(isOrigin > 0.5){
        gl_FragColor = abc;
    }else{

        //           gl_FragColor = vec4();
        //           vec4 mixColor =  vec4(mix(colorRGBTexture.rgb,abc.rgb,abc.a)*abc.a*mixCOEF,abc.a*mixCOEF);
        //           gl_FragColor = mixColor;
        //            gl_FragColor = vec4(abc.rgb*abc.a*mixCOEF,abc.a*mixCOEF);
        gl_FragColor = vec4(abc.rgb,abc.a*mixCOEF);
        //            if(isRenderColor > 0.0 ){
        //                  vec4 resultcolor = mix(colorRGBTexture,vec4(1.0,1.0,1.0,1.0),abc.a);
        //                  gl_FragColor = vec4(resultcolor.rgb*abc.a,abc.a*mixCOEF);
        //            }

        //           gl_FragColor = vec4(colorRGBTexture.rgb*abc.a*mixCOEF,abc.a*mixCOEF);
    }
}
