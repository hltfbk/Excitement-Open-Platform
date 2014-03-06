USE `wikipedialexicalrule`;

ALTER TABLE `rules` 
ADD INDEX `LeftTermIdx` (`leftTermId` ASC, `id` ASC) 
, ADD INDEX `RightTermIdx` (`rightTermId` ASC, `id` ASC) ;

ALTER TABLE rulepatterns
ADD INDEX posPattern (POSPattern (255) ASC) ;

ALTER TABLE rulepatterns
ADD INDEX relationPattern (relationsPattern(255) ASC) ;

ALTER TABLE rulepatterns
ADD INDEX POSrelationsPattern (POSrelationsPattern(255) ASC) ;
