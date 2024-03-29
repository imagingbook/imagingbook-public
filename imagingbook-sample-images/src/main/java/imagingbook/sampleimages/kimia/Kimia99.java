/*******************************************************************************
 * This software is provided as a supplement to the authors' textbooks on digital
 * image processing published by Springer-Verlag in various languages and editions.
 * Permission to use and distribute this software is granted under the BSD 2-Clause
 * "Simplified" License (see http://opensource.org/licenses/BSD-2-Clause).
 * Copyright (c) 2006-2023 Wilhelm Burger, Mark J. Burge. All rights reserved.
 * Visit https://imagingbook.com for additional details.
 ******************************************************************************/
package imagingbook.sampleimages.kimia;

import imagingbook.core.resource.ImageResource;

/**
 * A set of 99 binary shape images downloaded from https://vision.lems.brown.edu/content/available-software-and-databases
 * (http://vision.lems.brown.edu/sites/default/files/99db.tar.gz). These images were converted to PNG format, their
 * contents inverted and also their LUT's inverted. That is, pixels inside the contained shape(s) have foreground values
 * 255 (black) and the backgound value is 0 (white).
 *
 * @author WB
 * @see Kimia216
 * @see Kimia1070
 */
public enum Kimia99 implements ImageResource {
    bonefishes,
    bonefishesocc1,
    bunny04,
    calf1,
    calf2,
    cat1,
    cat2,
    cow1,
    cow2,
    desertcottontail,
    dog1,
    dog2,
    dog3,
    dogfishsharks,
    donkey1,
    dude0,
    dude1,
    dude10,
    dude11,
    dude12,
    dude2,
    dude4,
    dude5,
    dude6,
    dude7,
    dude8,
    easterncottontail,
    f15,
    f16,
    f16occ1,
    fgen1ap,
    fgen1bp,
    fgen1ep,
    fgen1fp,
    fgen2dp,
    fgen2fp,
    fgen3bp,
    fgen5cp,
    fish14,
    fish23,
    fish28,
    fish30,
    fox1,
    hand,
    hand2,
    hand2occ1,
    hand2occ2,
    hand2occ3,
    hand3,
    hand90,
    handbent1,
    handbent2,
    handdeform,
    handdeform2,
    harrier,
    harrierocc1,
    harrierocc2,
    harrierocc3,
    herrings,
    kk0728,
    kk0729,
    kk0731,
    kk0732,
    kk0735,
    kk0736,
    kk0737,
    kk0738,
    kk0739,
    kk0740,
    kk0741,
    marshrabbit,
    mgen1bp,
    mgen2ap,
    mgen2fp,
    mountaincottontail,
    mountaincottontailocc1,
    mountaincottontailocc2,
    mountaincottontailrot,
    mullets,
    phantom,
    phantomocc1,
    pygmyrabbit,
    skyhawk,
    skyhawkocc1,
    swamprabbit,
    swamprabbitocc2,
    swordfishes,
    tool04,
    tool04bent1,
    tool07,
    tool08,
    tool09,
    tool12,
    tool17,
    tool22,
    tool27,
    tool38,
    tool44,
    whalesharks,
    ;

}
