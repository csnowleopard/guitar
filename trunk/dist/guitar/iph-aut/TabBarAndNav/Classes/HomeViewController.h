//
//  HomeViewController.h
//  TabBarAndNav
//
//  Created by Holman on 2/19/12.
//  Copyright 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface HomeViewController : UIViewController {
	IBOutlet UILabel *nameLabel;
	IBOutlet UILabel *classLabel;
	IBOutlet UITextField *nameField;
}

@property (nonatomic, retain) UILabel *nameLabel;
@property (nonatomic, retain) UILabel *classLabel;
@property (nonatomic, retain) UITextField *nameField;

- (IBAction)changeGreeting:(id)sender;
- (IBAction)updateClass:(id)sender;

@end
