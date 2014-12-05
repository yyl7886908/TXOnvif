//
//  ViewController.m
//  IOS_Onvif_demo
//
//  Created by taixin on 14-12-5.
//  Copyright (c) 2014年 taixin. All rights reserved.
//

#import "ViewController.h"
#import "../../IOS_Onivf_Sdk/IOS_Onivf_Sdk/IOS_Onivf_Sdk.h"
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[IOS_Onivf_Sdk sharedManager] discoverDevices];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
