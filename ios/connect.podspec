#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint connect.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'connect'
  s.version          = '0.0.1'
  s.summary          = 'A new Flutter project.'
  s.description      = <<-DESC
A new Flutter project.
                       DESC
  s.homepage         = 'http://fuse.io'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Fuse' => 'murainoy@yahoo.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'WalletConnectSwift'
  s.dependency 'web3swift'
  s.vendored_libraries = 'web3swift','WalletConnectSwift'


  s.platform = :ios, '11.0'


  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'

end
