/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa.republertest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author ZuevAA
 */
@Service
@Qualifier("pyramidWeight")
public class PyramidWeight implements HumanEdgeWeight {
    final Integer WEIGHT = 50;
    public Double getHumanEdgeWeight(Integer level, Integer index) {
    Double parent_l, parent_r;
    if ((index < 0) || (index > level)) {
        throw new IllegalArgumentException(); 
    }
    if (level == 0) {
        return 0.0;
    }
    try {
        parent_l = (getHumanEdgeWeight(level-1, index-1)+WEIGHT)/2;
    }
    catch (IllegalArgumentException e) {
        parent_l = 0.0;
    }
    try {
        parent_r = (getHumanEdgeWeight(level-1, index)+WEIGHT)/2;
    }
    catch (IllegalArgumentException e) {
        parent_r = 0.0;
    }
    return parent_l + parent_r;
}
}
