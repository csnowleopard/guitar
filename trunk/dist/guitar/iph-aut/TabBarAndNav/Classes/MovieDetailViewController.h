//
//  MovieDetailViewController.h
//  TabBarAndNav
//
//  Created by Holman on 2/19/12.
//  Copyright 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface MovieDetailViewController : UIViewController {
	IBOutlet UILabel *label;
	NSString *movieSelected;
}

@property (nonatomic, retain) UILabel *label;
@property (nonatomic, retain) NSString *movieSelected;

@end
