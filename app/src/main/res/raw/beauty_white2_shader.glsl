
precision highp float;
varying highp vec2 textureCoordinate;
uniform sampler2D inputImageTexture;

const lowp vec3 weight = vec3(0.299,0.587,0.114);

uniform float myratio;
uniform lowp vec3 weight2;


vec4 adjustWhite(vec4 base,float ratio){
    float newR=pow(base.r,ratio);
    float newG=pow(base.g,ratio);
    float newB=pow(base.b,ratio);

    vec4 result=vec4(newR,newG,newB,base.a);
    return result;
}

float computeWhite(vec4 src){
    //    float gray=dot(src.rgb,weight2);
    //    float minWhite=1.0;
    //    float maxWhite=2.0-myratio;
    //    float whiteRatio=1.0;
    //
    //    float grayThreshold=0.35;
    //
    //    if(gray<grayThreshold){
    //        float a=(maxWhite-minWhite)/(grayThreshold*grayThreshold);
    //        whiteRatio=1.0-a*gray*gray;
    //    }
    //    else{
    //        whiteRatio=myratio;
    //    }
    //    return whiteRatio;
    return myratio;
}

vec4 adjustSaturation(vec4 src,float ratio){
    vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);
    float mixturePercent = 1.0;
    float luminance = dot(src.rgb, luminanceWeighting);
    vec3 greyScaleColor = vec3(luminance);
    vec4 textureColor3  = vec4(mix(greyScaleColor, src.rgb, ratio), src.w);
    vec4 result=textureColor3;
    return result;
}


float computeSaturation(vec4 src){
    float saturation=1.0;
    float gray=dot(src.rgb,weight2);
    float grayThreshold=0.35;

    if(gray<grayThreshold){
        saturation=1.2;
    }
    return saturation;
}

void main()
{
    vec4 curColor=texture2D(inputImageTexture,textureCoordinate);
    float whiteRatio=computeWhite(curColor);
    vec4 result=adjustWhite(curColor,whiteRatio);
    float saturation=computeSaturation(result);
    result=adjustSaturation(result,saturation);
    gl_FragColor = result;
}
