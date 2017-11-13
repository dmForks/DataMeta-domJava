#!/usr/bin/ruby

require 'dataMetaDom' # require the DataMeta Core
require 'dataMetaDom/pojo' # require the POJO+Comparators generator

require 'dataMetaJacksonSer'

include DataMetaDom, DataMetaDom::PojoLexer

JAVA_TARGET_DIR = 'src/test/java' # specify where to the Java code should be generated

def cleanFiles(rootDir, ext) # define the method for cleaning up Java and Scala files
  return unless File.directory?(rootDir) # if the root dir for this call does not exist, there is nothing to clean
  puts "Cleaning .#{ext} files from #{rootDir} and beyond..."
  Dir.glob("#{rootDir}/*.#{ext}").each {|f| # loop through the files with the given extension
    if IO.read(f) =~ %r{^\s*//\s+KEEP} # if the first line starts with "// KEEP" - then keep it
      puts "#{f} -- KEEP"
    else # otherwise delete it
      File.delete f
    end
  }
  # recursively dig into directories
  Dir.entries(rootDir).select {|e| File.directory?(File.join(rootDir, e))}.reject {|e| e.start_with?('.')}.each {|d|
    dirPath = File.join(rootDir, d)
    cleanFiles dirPath, ext
    # if no files with the extension ext (the parameter) left in the dir, delete it
    Dir.delete dirPath if Dir.entries(dirPath).reject {|e| e.start_with?('.')}.empty?
  }
end

cleanFiles File.join(JAVA_TARGET_DIR, %W(test ebay datameta ser jackson fasterxml gen)), 'java' # clean .java files

@m = Model.new

@m = @m.parse('src/test/resources/test.dmDom', options={autoVerNs: true})

genPojos(@m, JAVA_TARGET_DIR) # generate the POJOS

genDataMetaSames(@m, JAVA_TARGET_DIR , DataMetaDom::PojoLexer::FULL_COMPARE) #by all fields
genDataMetaSames(@m, JAVA_TARGET_DIR , DataMetaDom::PojoLexer::ID_ONLY_COMPARE) # by identity fields only

DataMetaJacksonSer::genJacksonables(@m, JAVA_TARGET_DIR, DataMetaJacksonSer::JAVA_FMT)
puts 'Done.'
